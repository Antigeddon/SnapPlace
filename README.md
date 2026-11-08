# SnapPlace Plugin for Minecraft Beta 1.7.3

SnapPlace is a plugin which improves block placement for **Minecraft Beta 1.7.3** by bringing modern **Minecraft** features (and more) to it.

## Features

- **Stackable Snow Layers**: Place multiple layers of snow on top of each other.  
- **Better Block Placements**: Rails can be oriented upon placement. Fences, pumpkins, and jack-o'-lanterns no longer require full block support underneath.  
- **Place on Interactables**: Allows placing blocks on interactables such as workbenches, chests, furnaces, dispensers, beds, buttons, and more.  
- **Improved Slab Placement**: Prevents slabs from merging unintentionally and supports placing slabs between existing slabs.  
- **Sign Editing**: Edit signs in-game using a command.  

All features are configurable and can be enabled or disabled.

## Installation

1. Download the latest SnapPlace release.  
2. Place the `jar` into your server's `plugins` folder.  
3. Start or restart your server.  
4. Adjust settings in the generated `SnapPlace/config.yml` to configure the plugin features.

## Commands

| Command    | Description                             | Permission         | Aliases  |
|------------|-------------------------------------|--------------------|----------|
| `/editsign` | Modify the text on signs with line-specific input | `SnapPlace.editsign` | `/esign` |


## Permissions

| Permission                           | Description                                                        | Default |
|------------------------------------|--------------------------------------------------------------------|---------|
| `SnapPlace.*`                      | Grants access to all SnapPlace features                            | op      |
| `SnapPlace.editsign`               | Allows players to edit sign text using the /editsign command       | true    |
| `SnapPlace.snowlayers`             | Allows placing snow layers on top of each other                    | true    |
| `SnapPlace.betterplacements.*`    | Grants access to all BetterPlacements features                     | true    |
| `SnapPlace.betterplacements.rails`| Allows orienting rails based on player direction                   | true    |
| `SnapPlace.betterplacements.wallandroof` | Allows placing fences, pumpkins, and jack-o'-lanterns on walls and roofs | true    |
| `SnapPlace.betterplacements.interactables` | Allows placing blocks on interactable blocks like workbenches and chests | true    |
| `SnapPlace.betterslabs`            | Allows improved slab placement including placing slabs between others and preventing unwanted merging | true    |


## Requirements

- Minecraft Beta 1.7.3  
- Poseidon V1/V2  
- Java 8 or higher  

## Configuration

The plugin’s `config.yml` allows you to configure all features, including enabling/disabling snow layer stacking, better placements, slab handling, and sign editing. You can also require players to sneak for these actions.
You have full control over what features to use.

## Contributing

Feel free to open issues or submit pull requests. Contributions and feedback are welcome!
