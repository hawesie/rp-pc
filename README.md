# Robot Programming Mobile Robot Classes

Some classes useful for robot sensing, simulation, testing map abstraction and localisation in leJOS. 

## Eclipse

### Getting the code

To use this code in your project you first need to clone the project into your Eclipse workspace. The following assumes you use the directory `~/workspace` as your Eclipse workspace as it is the default value. If this is not true, then replace this directory with the correct one for you. 

1. Open a terminal
2. Change into your workspace directory: `cd ~/workspace`
3. Clone this project using Git: `git clone https://github.com/hawesie/rp-pc`
4. In Eclipse, create a new leJOS PC project with the name `rp-pc`. This should automatically find the sources you just cloned. You could also create a standard Java project for this code.

If this project does not compile because it is missing any `lejos.*` packages this means that the leJOS PC jar was not added correctly. This happens to me, so it might happen to you too. This will also happen if you just created a standard Java project. To add the leJOS PC jar go to `Project` `Properties`, select `Java Build Path`, and click on the `Libraries` tab. From here click `Add Library...` select `LeJOS Library Container` then select platform `PC Libraries` and hit `Finish`.

### Dependencies


For the tests to compile, you must add JUnit support to your project. Go to `Project` `Properties`, select `Java Build Path`, and click on the `Libraries` tab. From here click `Add Library...` select `JUnit 4`.


### Using the code

You should develop your own code in a *separate project* to `rp-pc` as this will allow you to easily update the provided code if necessary. To do this, use the `Java Build Path` entry in your other project's properties, and `Add...` the `rp-pc` project under the `Projects` tab.

The interfaces are only provided as a guideline. If you want to ignore them, or change them (for the better), please feel free to do so. If you wish to change them, you can [fork this repository](https://github.com/hawesie/rp-pc/fork) to create your own copy. 

The following classes demonstrate the uses of the code from this repository:

 *  `rp.robotics.visualisation.DifferentialDriveSim` shows the simple simulation in action.
 * 

## Command Line

If you just want to use the code in compilation (e.g. from the command line) without Eclipse, you can download jar and use it in your classpath: https://raw.githubusercontent.com/hawesie/rp-pc/master/export/rp-pc.jar


## Fixing bugs

If you find any bugs in my code, please open an [issue](https://github.com/hawesie/rp-pc/issues) or create a pull request with the fix.
