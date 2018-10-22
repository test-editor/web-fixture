with import <nixpkgs> {};

let openjdk10_0_2 = import ./openjdk_10_0_2.nix;
in

stdenv.mkDerivation {
    name = "web-fixture";
    buildInputs = [
        openjdk10_0_2
        travis
        xvfb_run
        glibcLocales
        firefox-esr
        git
    ];
    shellHook = ''
        # do some gradle "finetuning"
        alias g="./gradlew"
        alias g.="../gradlew"
        export GRADLE_OPTS="$GRADLE_OPTS -Dorg.gradle.daemon=false -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
        export JAVA_TOOL_OPTIONS="$_JAVA_OPTIONS  -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
    '';
}
