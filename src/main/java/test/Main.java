package test;

import bean.AB;
import bean.B;
import bean.Vowel;
import contex.ApplicationContext;
import lombok.SneakyThrows;

public class Main {

  @SneakyThrows
  public static void main(String[] args) {
    ApplicationContext applicationContext = new ApplicationContext("bean");
    System.out.println(applicationContext.getBean(AB.class).doWord());
    System.out.println(applicationContext.getBean("AB", AB.class).doAB());
    applicationContext.getBeans(Vowel.class).forEach((s,v) -> System.out.println(v.makeNoise()));
    applicationContext.getBean("SusBena", B.class);
  }

}
