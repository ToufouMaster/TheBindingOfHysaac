rootProject.name = "The Binding of Hysaac"

plugins {
    // See documentation on https://scaffoldit.dev
    id("dev.scaffoldit") version "0.2.+"
}

// Would you like to do a split project?
// Create a folder named "common", then configure details with `common { }`

hytale {
    usePatchline("release")
    useVersion("latest")

    repositories {
        // Any external repositories besides: MavenLocal, MavenCentral, HytaleMaven, and CurseMaven
    }

    dependencies {
        // Any external dependency you also want to include
    }

    manifest {
        Group = "ToufouMaster"
        Name = "TheBindingOfHysaac"
        Main = "fr.toufoumaster.TheBindingOfHysaac"
        IncludesAssetPack = true
    }
}