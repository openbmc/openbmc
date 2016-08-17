
def get_navit_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU', 1) in [ 'soft' ]:
        return "--enable-avoid-float"
    return ""

