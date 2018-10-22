with import <nixpkgs> {};

let openjdk_10_0_2 = import ./openjdk_10_0_2.nix;
firefox_62_0_3 = stdenv.mkDerivation rec {
    name = "firefox_62_0_3";
    version = "62.0.3";
    src = fetchurl {
      url = "http://ftp.mozilla.org/pub/firefox/releases/62.0.3/linux-x86_64/en-qUS/firefox-62.0.3.tar.bz2";
      sha256 = "18is5gxywgbwczzdwd2mswrq7vb16jb1awif3j32zn25ifyz9zys";
    };

  installPhase = ''
    mkdir -p $out/bin
    cp -r ./* "$out/bin/"
    # correct interpreter and rpath for binaries to work
    find $out -type f -perm -0100 \
        -exec patchelf --interpreter "$(cat $NIX_CC/nix-support/dynamic-linker)" \;
   '';
};

in

stdenv.mkDerivation {
    name = "web-fixture";
    buildInputs = [
        openjdk_10_0_2
        travis
        xvfb_run
        glibcLocales
        firefox_62_0_3
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
