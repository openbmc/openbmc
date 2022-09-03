require optee-os.inc

SRCREV = "d21befa5e53eae9db469eba1685f5aa5c6f92c2f"

DEPENDS = "python3-pycryptodome-native python3-pyelftools-native"

SRC_URI:append = " \
    file://3.14/0009-add-z-execstack.patch \
    file://3.14/0010-add-note-GNU-stack-section.patch \
   "
