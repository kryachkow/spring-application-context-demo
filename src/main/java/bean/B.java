package bean;

import annotation.Bean;

@Bean(beanName = "beanB")
public class B {

  public String doB(){
    return "B";
  }

}
