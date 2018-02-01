
def get_edje_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU') in [ 'soft' ]:
        return "--enable-fixed-point"
    return ""

