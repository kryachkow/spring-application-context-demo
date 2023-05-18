package bean;

import annotation.Bean;

@Bean(beanName = "beanE")
public class E implements Vowel {

  @Override
  public String makeNoise() {
    return "EEE";
  }
}
