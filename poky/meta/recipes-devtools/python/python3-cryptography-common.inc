# Sharing PV between cryptography and cryptography-vectors will
# ensure that updating one but not the other will cause a build error.
#
# Additionally AUH will detect that they share this .inc file and
# perform a lockstep upgrade for both.
PV = "43.0.3"
