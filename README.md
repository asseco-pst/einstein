## Einstein

**A "runtime-dependencies" calculator.**
Given a Product on a given version, it calculates which projects - on which versions - must be sent  
with that Product version so it can be seamlessly executed.

### How to use

**Import** this project's **artifact** into your **own Projects** (!missing nexus artifact link here!)

``` groovy

  List<ProjectDao> projects = []
  
  // 'name' -> the project's name on the Gitlab repository
  // 'version' -> the version for which dependencies must be calculated
  projects << new ProjectDao(name: 'ebanka-venus', version: '2.5.0')
  // (...) add as much Projects as you want

  // calc dependencies
  Einstein.calcDependencies(projectsData)
  
  //it returns a Map containing the projects and versions 
  Map dependencies = Einstein.getResults()
  
```

Download the latest released artifact and run the command:

...