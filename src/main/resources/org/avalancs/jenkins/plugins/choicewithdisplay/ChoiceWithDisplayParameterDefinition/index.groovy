package org.avalancs.jenkins.plugins.choicewithdisplay.ChoiceWithDisplayParameterDefinition

def f = namespace(lib.FormTagLib)

f.entry(title: it.name, description: it.formattedDescription) { ->
    div(name: 'parameter') { ->
        input(name: 'name', type: 'hidden', value: it.name)
        select(name: 'value') { ->
            it.choices.each { choice ->
                f.option(value: choice.value) { -> // TODO: selected: choice.value==my.getDefaultValue() when default value is added
                    text(choice.key)
                }
            }
        }
    }
}