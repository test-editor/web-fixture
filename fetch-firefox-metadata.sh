#!/bin/bash
FFVERSION_LATEST="62.0.3"
FFVERSION_ESR="60.2.2esr"

function printMeta {
  rm SHA256SUMS &2>/dev/null || true
  VERSION=$1
  FFSHA256=$(wget -q http://download-origin.cdn.mozilla.net/pub/firefox/releases/$VERSION/SHA256SUMS && cat SHA256SUMS | grep linux-x86_64/en-US/firefox-$VERSION.tar.bz2 | awk '{ print $1; }')
  FFURL="http://ftp.mozilla.org/pub/firefox/releases/$1/linux-x86_64/en-US/firefox-$1.tar.bz2"

  echo "Download-URL: $FFURL"
  echo "SHA256: $FFSHA256"
}

echo "Firefox latest: $FFVERSION_LATEST"
printMeta $FFVERSION_LATEST
echo
echo "Firefox esr: $FFVERSION_ESR"
printMeta $FFVERSION_ESR
