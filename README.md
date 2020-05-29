# Soar-AoE

This repository is an implementation of a MultiAgent System using Soar: the Cognitive Architecture.

In this work, we aim to model a somewhat feudal society in which we will have a simple hierarchical structure,
composed of a noble or organiser, and minions that obey them. This society tries to mimic, up to a certain point,
the societies that we can build in civilisation evolution strategy video games such as Age of Empires.

## Authors

- [Víctor Giménez](https://github.com/KayandTheBlack)
- [Albert Rial](https://github.com/albertrial)
- [Josep de Cid](https://github.com/jdecid)

## Running the Code

### Running the BAT file

⚠️ Java x64 bits is required ⚠️

Run the BAT file double-clicking on it or from the CMD.

### Using IntelliJ IDEA

1. Import the project.
2. In `Project Structure > Modules > Dependencies` add the folders `libs` and `libs/soar/java`.
3. Create a new configuration for the `Main` class adding the environment variable `PATH=libs/soar`.

## Custom configuration

It is possible to create custom configurations for the simulations by creating or modifying the XML files in `resources > configuration` folder.
