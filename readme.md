### Choice with Display Parameter for Jenkins
This is a copy of the "Choice Parameter" included in Jenkins core, but with the added ability of showing different text in the dropdown in "Build with parameters" screen than the text passed to the actual build.<br/>
In the configuration when you enter the ```choices``` in each line, you first enter the value you want displayed in the dropdown, and if you want it to have a different value in the build you insert the ```Display-value separator``` character or string, then write the value.

For example, assuming the default ```|``` character used as ```Display-value separator```, the ```name``` of the parameter set to ```testParameter``` and the following set for ```choices```:
```
apple|a
pear|p
Company Website|example.org
```

And a freestyle build with an ```Execute shell``` step that just prints the value of the parameter:
```
echo $testParameter
```

The dropdown at the "Build with parameters" screen would have the following:
```
apple
pear
Company Website
```

If you chose ```Company Website``` and click on "Build" you would see ```example.org``` in the build log.

The ```choices``` are split at the first occurrence of ```Display-value separator```, so it is safe to use the same character in the value. Java's split() method is used with a limit of 2, so there is no regex or character escaping done.

This plugin still needs Unit tests! 

## License
The plugin uses the MIT license, as specified in the pom.xml file.