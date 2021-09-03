require ${BPN}.inc
DRIDRIVERS ??= ""
DRIDRIVERS:append:x86:class-target = ",r100,r200,nouveau,i965"
DRIDRIVERS:append:x86-64:class-target = ",r100,r200,nouveau,i965"

