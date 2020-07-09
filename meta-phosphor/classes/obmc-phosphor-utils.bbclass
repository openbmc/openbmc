def set_append(d, var, val, sep=' '):
    values = (d.getVar(var, True) or '').split(sep)
    if filter(bool, values):
        d.appendVar(var, '%s%s' %(sep, val))
    else:
        d.setVar(var, val)


def listvar_to_list(d, list_var, sep=' '):
    return list(filter(bool, (d.getVar(list_var, True) or '').split(sep)))


def compose_list(d, fmtvar, *listvars, **kw):
    import itertools
    fmt = d.getVar(fmtvar, True)
    lists = [listvar_to_list(d, x) for x in listvars]
    lst = [fmt.format(*x) for x in itertools.product(*lists)]
    return (kw.get('sep') or ' ').join(lst)


def compose_list_zip(d, fmtvar, *listvars, **kw):
    fmt = d.getVar(fmtvar, True)
    lists = [listvar_to_list(d, x) for x in listvars]
    lst = [fmt.format(*x) for x in zip(*lists)]
    return (kw.get('sep') or ' ').join(lst)
