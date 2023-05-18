package bean;

import annotation.Bean;
import annotation.Inject;
import annotation.Qualifier;

@Bean(beanName = "AB")
public class AB {

  private final A a;
  private final B b;

  @Inject
  @Qualifier(name = "beanE")
  private Vowel vowel1;

  private final Vowel vowel2;

  public AB(A a, B b, @Qualifier(name = "beanO") Vowel vowel){
    this.a = a;
    this.b = b;
    vowel2 = vowel;
  }

  public String doAB(){
    return a.doA() + b.doB();
  }
  public String doWord(){ return a.doA() + vowel1.makeNoise() + vowel2.makeNoise();}


}
