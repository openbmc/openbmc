
def get_navit_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU') in [ 'soft' ]:
        return "--enable-avoid-float"
    return ""

