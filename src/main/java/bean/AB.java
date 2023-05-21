package bean;

import annotation.Bean;
import annotation.Inject;
import annotation.Qualifier;
import annotation.Value;

@Bean(beanName = "AB")
public class AB {

  private final A a;
  private final B b;

  @Inject
  private Vowel vowel1;

  private final Vowel vowel2;

  @Value(value = "bean.AB.value")
  private String value;

  public AB(A a, B b, Vowel vowel){
    this.a = a;
    this.b = b;
    vowel2 = vowel;
  }

  public String doAB(){
    return a.doA() + b.doB();
  }
  public String doWord(){ return a.doA() + vowel1.makeNoise() + vowel2.makeNoise() + value;}


}
