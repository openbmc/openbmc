# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from . import OETestFilter, registerDecorator
from oeqa.core.utils.misc import intToList

def _idFilter(oeid, filters):
     return False if oeid in filters else True

@registerDecorator
class OETestID(OETestFilter):
    attrs = ('oeid',)

    def bind(self, registry, case):
        super(OETestID, self).bind(registry, case)

    def filtrate(self, filters):
        if filters.get('oeid'):
            filterx = intToList(filters['oeid'], 'oeid')
            del filters['oeid']
            if _idFilter(self.oeid, filterx):
                return True
        return False
