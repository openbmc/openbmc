inherit setuptools3

do_compile:class-native() {
    :
}

do_install:class-native() {
    :
}

DEPENDS:append:class-target = " python"

inherit skeleton
inherit allarch
