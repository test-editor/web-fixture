# web-fixture

[![License](http://img.shields.io/badge/license-EPL-blue.svg?style=flat)](https://www.eclipse.org/legal/epl-v10.html)
[![Build Status](https://travis-ci.org/test-editor/web-fixture.svg?branch=master)](https://travis-ci.org/test-editor/web-fixture)
[![Download](https://api.bintray.com/packages/test-editor/Fixtures/web-fixture/images/download.svg)](https://bintray.com/test-editor/Fixtures/web-fixture/_latestVersion)

## Development

### Setup development

Make sure to have a working [nix](https://nixos.org/nix/) installation. Please ensure that the `nixpkgs-unstable` channel is available. It
can be added with `nix-channel --add https://nixos.org/channels/nixpkgs-unstable`.

To enter the development environment, execute `NIXPKGS_ALLOW_UNFREE=1 nix-shell` in this repos root directory. For even more convenience,
please install [direnv](https://github.com/direnv/direnv) which will enter the development environment automatically for you.

Once within the development environment, run `./gradlew build` to resolve all necessary dependencies and run all checks.

    
### Build

    git submodule update --init --recursive
    ./gradlew build

### Release process

In order to create a release switch to the `master` branch and execute

    ./gradlew release

and enter the new version. After the commit and tag is pushed Travis will automatically build and deploy the tagged version to Bintray.

### Browser Setup

please refer to [Documentation for browser specific testing with options and capabilities](docs/BROWSERSETUP.md)
