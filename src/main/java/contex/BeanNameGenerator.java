package contex;

import annotation.Bean;

public class BeanNameGenerator {

   protected static <T> String getBeanName(Class<T> tClass) {

    return tClass.getAnnotation(Bean.class).beanName().equals("") ?
        fistCharToLower(tClass.getSimpleName()) :
        tClass.getAnnotation(Bean.class).beanName();
   }

   private static String fistCharToLower(String string) {
     char c[] = string.toCharArray();
     c[0] = Character.toLowerCase(c[0]);
    return new String(c);
   }

}
