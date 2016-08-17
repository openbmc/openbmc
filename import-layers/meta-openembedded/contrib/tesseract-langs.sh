#! /bin/sh

# Copyright (C) 2014, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see meta-openembedded layer's COPYING.MIT)

PV='3.02'

# Sometimes the software package has a minor version, but language
# packages have not.  Example: 
#   software package: tesseract-ocr-3.02.02.tar.gz
#   language package: tesseract-ocr-3.02.por.tar.gz
MINOR_PV=02

recipes_dir=$1

usage() {
    echo "Usage: `basename $0` <recipes dir> [ <download dir> ]"
}

if [ -z "$recipes_dir" ]; then
    usage
    exit 1
fi
mkdir -p "$recipes_dir"

file_list_uri='https://code.google.com/p/tesseract-ocr/downloads/list'
file_list=`mktemp`

remove_dl_dir=
if [ -z "$2" ]; then
    remove_dl_dir=1
    dl_dir=`mktemp -d`
else
    dl_dir="$2"
fi

mkdir -p $dl_dir

tesseract_langs() {
    wget -q -O "$file_list" "$file_list_uri"

    grep -E 'a href="detail\?name=tesseract-ocr-'${PV}'\.[^\.]+.tar.gz&amp;can=2&amp;q=">' "$file_list" | \
        sed -r -e 's/.*tesseract-ocr-'${PV}'\.*([^\.]+)\.tar\.gz.*/\1/' | \
        grep -Ev '('${MINOR_PV}'|'${MINOR_PV}'-doc-html)' | \
        sort -u
}

download_lang_files() {
    local langs="$1"
    local uri
    for lang in $langs; do
        if [ ! -e "$dl_dir/tesseract-ocr-${PV}.${lang}.tar.gz" ]; then
            uri="https://tesseract-ocr.googlecode.com/files/tesseract-ocr-${PV}.${lang}.tar.gz"
            echo "Downloading $uri"
            wget -q -P "$dl_dir" "$uri"
        fi
    done
}

create_recipe() {
    local lang=$1
    local tarball

    tarball="$dl_dir/tesseract-ocr-${PV}.${lang}.tar.gz"

    md5sum=`md5sum $tarball | awk '{print $1}'`
    sha256sum=`sha256sum $tarball | awk '{print $1}'`

    cat > $recipes_dir/tesseract-lang-`echo ${lang} | sed s/_/-/g`_${PV}.bb <<EOF
# Copyright (C) 2014, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see meta-openembedded layer's COPYING.MIT)

TESSERACT_LANG = "$lang"

require tesseract-lang.inc

SRC_URI[md5sum] = "${md5sum}"
SRC_URI[sha256sum] = "${sha256sum}"
EOF
}


LANGS=`tesseract_langs`

download_lang_files "$LANGS"

for lang in $LANGS; do
    create_recipe $lang
done

[ -n "$remove_dl_dir" ] && rm -rf $dl_dir
rm -f $file_list
