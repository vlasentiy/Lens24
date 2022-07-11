package demo.validation;

import android.text.TextUtils;

import demo.R;

import static demo.validation.FieldValidationResult.fail;
import static demo.validation.FieldValidationResult.valid;

public class CardHolderValidator implements FieldValidator<CharSequence> {
    @Override
    public FieldValidationResult validate(CharSequence value) {
        if (TextUtils.isEmpty(value)) {
            return fail(R.string.validation_error_fill_in_card_holder_name);
        }
        if (!value.toString().matches("^((?:[A-Za-z]+ ?){1,3})$")) {
            return fail(R.string.validation_error_invalid_card_holder_name);
        }
        return valid();
    }
}
