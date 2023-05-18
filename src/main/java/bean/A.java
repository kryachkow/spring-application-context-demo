package bean;

import annotation.Bean;

@Bean(beanName = "beanA")
public class A {

  String doA(){
    return "A";
  }

}
