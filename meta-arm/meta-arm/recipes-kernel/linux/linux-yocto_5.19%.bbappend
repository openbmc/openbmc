# enable arm_ffa regardless on 5.19
SRC_URI:append:qemuarm = " \
    file://tee.cfg \
    file://arm-ffa-transport.cfg \
"
SRC_URI:append:qemuarm64 = " \
    file://tee.cfg \
    file://arm-ffa-transport.cfg \
"
