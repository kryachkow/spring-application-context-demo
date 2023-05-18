package contex;

import annotation.Bean;
import annotation.Inject;
import annotation.Qualifier;
import exception.InappropriateInjectionException;
import exception.NoSuchBeanException;
import exception.NoUniqueBeanException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections8.Reflections;

public class ApplicationContext {

  private final Map<String, Object> beans = new HashMap<>();

  public ApplicationContext(String path)
      throws NoSuchBeanException, NoUniqueBeanException, InvocationTargetException, InstantiationException, IllegalAccessException {
    putBeansInAppContext(new Reflections(path));
    makeBeanInjection();
  }

  public <T> T getBean(Class<T> tClass) throws NoSuchBeanException, NoUniqueBeanException {
    return tClass.isInterface() ? getBeanForInterface(tClass) : getBeanForClass(tClass);
  }

  private <T> T getBeanForClass(Class<T> tClass) throws NoSuchBeanException {
    return beans
        .values()
        .stream()
        .filter(tClass::isInstance)
        .map(o -> (T) o)
        .findFirst()
        .orElseThrow(() -> new NoSuchBeanException("No bean for " + tClass.getName() + " class"));
  }

  private <T> T getBeanForInterface(Class<T> tClass)
      throws NoUniqueBeanException, NoSuchBeanException {
    List<T> collect = beans
        .values()
        .stream()
        .filter(tClass::isInstance)
        .map(o -> (T) o)
        .collect(Collectors.toList());
    if (collect.size() > 1) {
      throw new NoUniqueBeanException("No unique bean for " + tClass.getName() + "class");
    }
    return Optional.of(collect.get(0))
        .orElseThrow(() -> new NoSuchBeanException("No bean for " + tClass.getName() + " class"));

  }

  public <T> T getBean(String beanName, Class<T> tClass) throws NoSuchBeanException {
    Object o = beans.get(beanName);
    if (tClass.isInstance(o)) {
      return (T) o;
    }
    throw new NoSuchBeanException();
  }

  public <T> Map<String, T> getBeans(Class<T> tClass) {
    return beans.entrySet()
        .stream()
        .filter(e -> tClass.isInstance(e.getValue()))
        .collect(Collectors.toMap(Entry::getKey, e -> (T) e.getValue()));
  }


  private void putBeansInAppContext(Reflections reflections)
      throws NoSuchBeanException, NoUniqueBeanException, InvocationTargetException, InstantiationException, IllegalAccessException {
    Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Bean.class);
    for (Object beanClass : typesAnnotatedWith.toArray()) {
      createBean((Class<?>) beanClass, typesAnnotatedWith);
    }
  }


  private <T> T createBean(Class<T> tClass, Set<Class<?>> beansClasses)
      throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchBeanException, NoUniqueBeanException {
    if (beans.containsKey(BeanNameGenerator.getBeanName(tClass))) {
      return (T) beans.get(BeanNameGenerator.getBeanName(tClass));
    }
    Constructor<T> constructor = (Constructor<T>) tClass.getConstructors()[0];
    List<Object> params = new ArrayList<>();
    for (Parameter param : constructor.getParameters()) {
      if (param.getType().isInterface()) {
        params.add(createBeanToInterface(param, beansClasses));
        continue;
      }
      Class<?> paramClass = param.getType();
      if (!paramClass.isAnnotationPresent(Bean.class)) {
        throw new InappropriateInjectionException("Bean is out of context");
      }
      if (!beans.containsKey(BeanNameGenerator.getBeanName(paramClass))) {
        createBean(paramClass, beansClasses);
      }
      params.add(beans.get(BeanNameGenerator.getBeanName(paramClass)));
    }
    T retValue = constructor.newInstance(params.toArray());
    beans.put(BeanNameGenerator.getBeanName(tClass),
        retValue);
    return retValue;
  }

  private <T> T createBeanToInterface(Parameter parameter, Set<Class<?>> beansClasses)
      throws NoUniqueBeanException, NoSuchBeanException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (parameter.isAnnotationPresent(Qualifier.class)) {
      String name = parameter.getAnnotation(Qualifier.class).name();
      Optional<Class<?>> neededBean = beansClasses
          .stream()
          .filter(findClass -> findClass.getAnnotation(Bean.class).beanName().equals(name))
          .findFirst();
      return (T) createBean(
          neededBean.orElseThrow(() -> new NoSuchBeanException("No bean for " + name + " class")),
          beansClasses);
    }
    List<Class<?>> collect = beansClasses
        .stream()
        .filter(
            findClass -> Arrays.asList(findClass.getInterfaces()).contains(parameter.getClass()))
        .collect(Collectors.toList());
    if (collect.size() > 1) {
      throw new NoUniqueBeanException(
          "No unique bean for " + parameter.getClass().getName() + "class");
    }
    return (T) createBean(Optional.of(collect.get(0)).orElseThrow(
            () -> new NoSuchBeanException("No bean for " + parameter.getClass().getName() + " class")),
        beansClasses);
  }


  private void makeBeanInjection()
      throws NoSuchBeanException, NoUniqueBeanException, IllegalAccessException {
    for (Object o : beans.values()) {

      for (Field field : Arrays.stream(o.getClass().getDeclaredFields())
          .filter(field -> field.isAnnotationPresent(Inject.class))
          .collect(Collectors.toList())) {
        injectToField(field, o);
      }
    }
  }

  private void injectToField(Field field, Object object)
      throws IllegalAccessException, NoSuchBeanException, NoUniqueBeanException {
    field.setAccessible(true);
    if (fieldQualified(field)) {
      field.set(object, beans.get(field.getAnnotation(Qualifier.class).name()));
    } else {
      Object injection = field.getType().isInterface() ? getBeanForInterface(field.getType())
          : getBeanForClass(field.getType());
      field.set(object, injection);
    }
  }

  private boolean fieldQualified(Field field) {
    return field.isAnnotationPresent(Qualifier.class);
  }
}

