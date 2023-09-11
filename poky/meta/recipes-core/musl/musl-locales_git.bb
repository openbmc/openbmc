# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)
#
SUMMARY = "Locales support for musl"
HOMEPAGE = "https://git.adelielinux.org/adelie/musl-locales/-/wikis/home"
LICENSE = "MIT & LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf5713fba707073020b1db2acaa73e78 \
                    file://LICENSE.MIT;md5=a4f1c6864a83ddf4b754cdab7d593523"

SRC_URI = "git://git.adelielinux.org/adelie/musl-locales;protocol=https;branch=main"

PV = "1.0+git"
SRCREV = "5663f5bfd30bf9e1e0ba3fc5fe2da6725969f30e"

S = "${WORKDIR}/git"

DEPENDS = "virtual/libintl gettext-native"

PROVIDES = "virtual/libc-locale"

inherit cmake

# We will skip parsing for non-musl systems
python () {
    if d.getVar('TCLIBC') != "musl":
        raise bb.parse.SkipRecipe("Only use it with Musl C library")
}

# only locale binaries are under GPL-3.0-or-later others are MIT
LICENSE:${PN} = "LGPL-3.0-or-later"
LICENSE:locale-base-cs-cz = "MIT"
LICENSE:locale-base-de-ch = "MIT"
LICENSE:locale-base-de-de = "MIT"
LICENSE:locale-base-en-gb = "MIT"
LICENSE:locale-base-en-us = "MIT"
LICENSE:locale-base-es-es = "MIT"
LICENSE:locale-base-fi-fi = "MIT"
LICENSE:locale-base-fr-ca = "MIT"
LICENSE:locale-base-fr-fr = "MIT"
LICENSE:locale-base-it-it = "MIT"
LICENSE:locale-base-nb-no = "MIT"
LICENSE:locale-base-nl-nl = "MIT"
LICENSE:locale-base-pt-br = "MIT"
LICENSE:locale-base-pt-pt = "MIT"
LICENSE:locale-base-ru-ru = "MIT"
LICENSE:locale-base-sr-rs = "MIT"
LICENSE:locale-base-sv-se = "MIT"

PACKAGES =+ "locale-base-cs-cz \
             locale-base-de-ch \
             locale-base-de-de \
             locale-base-en-gb \
             locale-base-en-us \
             locale-base-es-es \
             locale-base-fi-fi \
             locale-base-fr-ca \
             locale-base-fr-fr \
             locale-base-it-it \
             locale-base-nb-no \
             locale-base-nl-nl \
             locale-base-pt-br \
             locale-base-pt-pt \
             locale-base-ru-ru \
             locale-base-sr-rs \
             locale-base-sv-se \
             "
FILES:locale-base-cs-cz += "${datadir}/i18n/locales/musl/cs_CZ.UTF-8"
FILES:locale-base-de-ch += "${datadir}/i18n/locales/musl/de_CH.UTF-8"
FILES:locale-base-de-de += "${datadir}/i18n/locales/musl/de_DE.UTF-8"
FILES:locale-base-en-gb += "${datadir}/i18n/locales/musl/en_GB.UTF-8"
FILES:locale-base-en-us += "${datadir}/i18n/locales/musl/en_US.UTF-8"
FILES:locale-base-es-es += "${datadir}/i18n/locales/musl/es_ES.UTF-8"
FILES:locale-base-fi-fi += "${datadir}/i18n/locales/musl/fi_FI.UTF-8"
FILES:locale-base-fr-ca += "${datadir}/i18n/locales/musl/fr_CA.UTF-8"
FILES:locale-base-fr-fr += "${datadir}/i18n/locales/musl/fr_FR.UTF-8"
FILES:locale-base-it-it += "${datadir}/i18n/locales/musl/it_IT.UTF-8"
FILES:locale-base-nb-no += "${datadir}/i18n/locales/musl/nb_NO.UTF-8"
FILES:locale-base-nl-nl += "${datadir}/i18n/locales/musl/nl_NL.UTF-8"
FILES:locale-base-pt-br += "${datadir}/i18n/locales/musl/pt_BR.UTF-8"
FILES:locale-base-pt-pt += "${datadir}/i18n/locales/musl/pt_PT.UTF-8"
FILES:locale-base-ru-ru += "${datadir}/i18n/locales/musl/ru_RU.UTF-8"
FILES:locale-base-sr-rs += "${datadir}/i18n/locales/musl/sr_RS.UTF-8"
FILES:locale-base-sv-se += "${datadir}/i18n/locales/musl/sv_SE.UTF-8"

UPSTREAM_CHECK_COMMITS = "1"
