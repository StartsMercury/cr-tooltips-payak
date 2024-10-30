# CR Tooltips Payák

This is a simple tooltip mod, mainly extending the current tooltips to the item
catalog and when switching with the hotbar.

## Downloads

Cosmic Light Bleed Fix is only officially available on CRMM and GitHub. All
published version of this mod are both in
[CRMM](https://www.crmm.tech/mod/cr-tooltips-payak/versions) and
[GitHub Releases](https://github.com/StartsMercury/cr-tooltips-payak/releases):

> * <https://www.crmm.tech/mod/cr-tooltips-payak/versions>
> * <https://github.com/StartsMercury/cr-tooltips-payak/releases>

---

This repository is generated from
https://codeberg.org/CRModders/cosmic-quilt-example.

## Wiki

For a wiki on how to use Cosmic Quilt & Quilt, please look at the [Cosmic Quilt
wiki] .

## How to test/build

For testing in the developer environment, you can use the `./gradlew run` task

For building, the usual `./gradlew build` task can be used. The mod jars will be
in the `build/libs/` folder

## Notes
- Most project properties can be changed in the <tt>[gradle.properties]</tt>
- To change author, description and stuff that is not there, edit <tt>[src/main/resources/quilt.mod.json]</tt>
- The project name is defined in <tt>[settings.gradle.kts]</tt>
- To add Quilt mods in the build, make sure to use `internal` rather than `implementation`

[src/main/resources/quilt.mod.json]: src/main/resources/quilt.mod.json
[gradle.properties]: gradle.properties
[settings.gradle.kts]: settings.gradle.kts

[Cosmic Quilt wiki]: https://codeberg.org/CRModders/cosmic-quilt/wiki
