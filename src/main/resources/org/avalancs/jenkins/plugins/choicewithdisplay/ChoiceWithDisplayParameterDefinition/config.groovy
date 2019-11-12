package org.avalancs.jenkins.plugins.choicewithdisplay.ChoiceWithDisplayParameterDefinition

def l = namespace(lib.LayoutTagLib)
def f=namespace(lib.FormTagLib)

f.entry(title:_('Name'), field: 'name') {
    f.textbox(name: 'parameter.name', value: "${instance.name}")
}

f.entry(title:_('Display_Value_Separator'), field: 'display_value_separator') {
    f.textbox(name: 'parameter.displayValueSeparator', value: "${instance?.displayValueSeparator}", default: '|', clazz: 'required')
}

f.entry(title:_('Choices'), field: 'choices') {
    f.textarea(name: 'parameter.choices', value: "${instance.choicesText}",
            checkUrl: "${rootURL}/descriptorByName/org.avalancs.jenkins.plugins.choicewithdisplay.ChoiceWithDisplayParameterDefinition/checkChoices?value='+encodeURIComponent(this.value)")
}

f.entry(title:_('Description'), field: 'description') {
    f.textarea(name: 'parameter.description', value: "${instance?.description}",
            'codemirror-mode': "${app.markupFormatter.codeMirrorMode}",
            'codemirror-config': "${app.markupFormatter.codeMirrorConfig}",
            previewEndpoint: "/markupFormatter/previewDescription")
}