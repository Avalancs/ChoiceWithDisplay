package org.avalancs.jenkins.plugins.choicewithdisplay;

import hudson.Extension;
import hudson.model.*;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author avalancs
 * Based on ChoiceParameterDefinition from jenkins core
 */
public class ChoiceWithDisplayParameterDefinition extends SimpleParameterDefinition {
    public static final String CHOICES_DELIMITER = "\\r?\\n";

    protected Map<String, String> choices;
    protected final String defaultValue;

    /**
     * The string used as delimiter between the display value and the actual value in a single line.<br>
     * e.g.: if the displayValueSeparator is the pipe character <b>|</b> and the line is <b>apple|a</b>
     * then the generated html option will be:<br>
     * &lt;option value="<b>a</b>"&gt;<b>apple</b>&lt;/option&gt;
     */
    protected String displayValueSeparator;

    public static boolean areValidChoices(String choices) {
        String strippedChoices = choices.trim();
        return !StringUtils.isEmpty(strippedChoices) && strippedChoices.split(CHOICES_DELIMITER).length > 0;
    }

    private ChoiceWithDisplayParameterDefinition(String name, String choicesStr, String displayValueSeparator, String description) {
        this(name, description);
        this.displayValueSeparator = displayValueSeparator;
        setChoices(choicesStr);
    }

    private ChoiceWithDisplayParameterDefinition(String name, Map<String, String> newChoices, String defaultValue, String displayValueSeparator, String description) {
        super(name, description);
        this.choices = newChoices;
        this.defaultValue = defaultValue;
        this.displayValueSeparator = displayValueSeparator;
    }

    /**
     * Databound constructor for reflective instantiation.
     * @param name parameter name
     * @param description parameter description
     */
    @DataBoundConstructor
    @Restricted(NoExternalUse.class)
    public ChoiceWithDisplayParameterDefinition(String name, String description) {
        super(name, description);
        this.choices = new HashMap<>();
        this.defaultValue = null;
    }

    /**
     * Set the list of choices.
     */
    @DataBoundSetter
    @Restricted(NoExternalUse.class)
    public void setChoices(String choicesStr) {
        choices.clear();

        if(displayValueSeparator == null || StringUtils.isEmpty(displayValueSeparator)) {
            displayValueSeparator = "|";
        }

        for(String line : choicesStr.split(CHOICES_DELIMITER)) {
            String[] val = line.split(Pattern.quote(displayValueSeparator), 2);
            if(val.length == 1) {
                choices.put(val[0], val[0]);
            } else if(val.length == 2) {
                choices.put(val[0], val[1]);
            }
        }
    }

    @Exported
    public Map<String, String> getChoices() {
        return choices;
    }

    /**
     * Used in the jelly file to fill out the text field
     */
    public String getChoicesText() {
        StringBuilder sb = new StringBuilder();
        choices.forEach((key, val) -> {
            if(key.equals(val)) {
                sb.append(key + System.lineSeparator());
            } else {
                sb.append(key + displayValueSeparator + val + System.lineSeparator());
            }
        });
        if(sb.toString().endsWith(System.lineSeparator())) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    @DataBoundSetter
    @Restricted(NoExternalUse.class)
    public void setDisplayValueSeparator(String newSeparator) {
        this.displayValueSeparator = newSeparator;
    }

    @Exported
    public String getDisplayValueSeparator() {
        return displayValueSeparator;
    }

    @Override
    public ParameterDefinition copyWithDefaultValue(ParameterValue defaultValue) {
        if (defaultValue instanceof StringParameterValue) {
            StringParameterValue value = (StringParameterValue) defaultValue;
            return new ChoiceWithDisplayParameterDefinition(getName(), choices, (String)value.getValue(), this.displayValueSeparator, getDescription());
        } else {
            return this;
        }
    }

    @Override
    public StringParameterValue getDefaultParameterValue() {
        String firstChoice = "";
        Iterator<String> it = choices.keySet().iterator();
        if(it.hasNext()) {
            firstChoice = it.next();
        }
        return new StringParameterValue(getName(), defaultValue == null ? firstChoice : defaultValue, getDescription());
    }

    private StringParameterValue checkValue(StringParameterValue value) {
        if (!choices.values().contains(value.getValue()))
            throw new IllegalArgumentException("Illegal choice for parameter " + getName() + ": " + value.getValue());
        return value;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
        value.setDescription(getDescription());
        return checkValue(value);
    }

    public StringParameterValue createValue(String value) {
        return checkValue(new StringParameterValue(getName(), value, getDescription()));
    }

    @Extension
    @Symbol({"choiceWithDisplay","choiceWithDisplayParam"})
    public static class DescriptorImpl extends ParameterDescriptor {
        public DescriptorImpl() {
            super(ChoiceWithDisplayParameterDefinition.class);
        }

        @Override
        public String getDisplayName() {
            return Messages.ChoiceWithDisplayParameterDefinition_DisplayName();
        }

        @Override
        public ParameterDefinition newInstance(@Nullable StaplerRequest req, @Nonnull JSONObject formData) throws FormException {
            String name = formData.getString("name");
            String displayValueSeparator = formData.getString("displayValueSeparator");
            String desc = formData.getString("description");
            String choicesText = formData.getString("choices");
            return new ChoiceWithDisplayParameterDefinition(name, choicesText, displayValueSeparator, desc);
        }

        /**
         * Checks if parameterized build choices are valid.
         */
        public FormValidation doCheckChoices(@QueryParameter String value) {
            if (ChoiceWithDisplayParameterDefinition.areValidChoices(value)) {
                return FormValidation.ok();
            } else {
                return FormValidation.error(Messages.ChoiceWithDisplayParameterDefinition_MissingChoices());
            }
        }
    }

}