package bean;

import annotation.Bean;

@Bean(beanName = "beanO")
public class O implements Vowel {


  @Override
  public String makeNoise() {
    return "OOOOOO";
  }
}
