package demo.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import demo.R;
import demo.utils.StringUtil;

import static demo.validation.FieldValidationResult.fail;
import static demo.validation.FieldValidationResult.valid;

public class CardExpiryDateValidator implements FieldValidator<CharSequence> {

    private static final Pattern sExpiryDatePattern = Pattern.compile("([0-9]{2})/([0-9]{2})");

    @Override
    public FieldValidationResult validate(CharSequence value) {
        if (StringUtil.isBlank(value)) {
            return fail(R.string.validation_error_fill_in_expiry_date);
        }

        Matcher matcher = sExpiryDatePattern.matcher(value);
        if (!matcher.matches()) {
            return fail(R.string.validation_error_invalid_expiry_date);
        }

        if (!isValidMm(matcher.group(1))
                || !isValidYy(matcher.group(2))) {
            return fail(R.string.validation_error_invalid_expiry_date);
        }

        return valid();
    }

    private static boolean isValidMm(String mm) {
        int month = Integer.parseInt(mm);
        return (month <= 12 && month > 0);
    }

    private static boolean isValidYy(String yy) {
        int year = Integer.parseInt(yy);
        return (year >= 0 && year <= 99);
    }

}
