package pl.zenit.tuqan.generators.literals.php;

import pl.zenit.tuqan.generators.literals.DefaultField;
import pl.zenit.tuqan.lang.struct.TuqanFieldBroker;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;

public class PhpField extends DefaultField {

    public PhpField(TuqanFieldBroker f) {
        super(f);
    }

    @Override public String type() throws GenerationFuckup {
        throw new GenerationFuckup("PHP uses no var types");
    }

}
