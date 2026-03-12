DESCRIPTION = "Doxygen is the de facto standard tool for generating documentation from annotated C++ sources."
HOMEPAGE = "http://www.doxygen.org/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "bison-native flex-native"

SRC_URI = "\
    git://github.com/doxygen/doxygen.git;branch=master;protocol=https \
"

SRCREV = "669aeeefca743c148e2d935b3d3c69535c7491e6"


UPSTREAM_CHECK_GITTAGREGEX = "Release_(?P<pver>\d+(\_\d+)+)"

inherit cmake python3native

EXTRA_OECMAKE += "\
    -DFLEX_TARGET_ARG_COMPILE_FLAGS='--noline' \
    -DBISON_TARGET_ARG_COMPILE_FLAGS='--no-lines' \
"

do_install:append() {
    sed -i -e 's;^#ifndef.*GENERATED_SRC_MSCGEN_LANGUAGE_HPP_INCLUDED$;#ifndef GENERATED_SRC_MSCGEN_LANGUAGE_HPP_INCLUDED;g' ${B}/generated_src/mscgen_language.hpp
    sed -i -e 's;^# define.*GENERATED_SRC_MSCGEN_LANGUAGE_HPP_INCLUDED$;# define GENERATED_SRC_MSCGEN_LANGUAGE_HPP_INCLUDED;g' ${B}/generated_src/mscgen_language.hpp
    sed -i -e 's;^#endif.*GENERATED_SRC_MSCGEN_LANGUAGE_HPP_INCLUDED.*$;#endif;g' ${B}/generated_src/mscgen_language.hpp

    sed -i -e 's;^#ifndef.*GENERATED_SRC_CE_PARSE_HPP_INCLUDED$;#ifndef GENERATED_SRC_CE_PARSE_HPP_INCLUDED;g' ${B}/generated_src/ce_parse.hpp
    sed -i -e 's;^# define.*GENERATED_SRC_CE_PARSE_HPP_INCLUDED$;# define GENERATED_SRC_CE_PARSE_HPP_INCLUDED;g' ${B}/generated_src/ce_parse.hpp
    sed -i -e 's;^#endif.*GENERATED_SRC_CE_PARSE_HPP_INCLUDED.*$;#endif;g' ${B}/generated_src/ce_parse.hpp
}

BBCLASSEXTEND = "native nativesdk"
