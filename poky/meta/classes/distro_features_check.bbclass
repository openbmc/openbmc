# Temporarily provide fallback to the old name of the class

python __anonymous() {
    bb.warn("distro_features_check.bbclass is deprecated, please use features_check.bbclass instead")
}

inherit features_check
