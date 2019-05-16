#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.contrib import admin
from orm.models import BitbakeVersion, Release, ToasterSetting, Layer_Version
from django import forms
import django.db.models as models


class BitbakeVersionAdmin(admin.ModelAdmin):

    # we override the formfield for db URLField
    # because of broken URL validation

    def formfield_for_dbfield(self, db_field, **kwargs):
        if isinstance(db_field, models.fields.URLField):
            return forms.fields.CharField()
        return super(BitbakeVersionAdmin, self).formfield_for_dbfield(
            db_field, **kwargs)


class ReleaseAdmin(admin.ModelAdmin):
    pass


class ToasterSettingAdmin(admin.ModelAdmin):
    pass


class LayerVersionsAdmin(admin.ModelAdmin):
    pass

admin.site.register(Layer_Version, LayerVersionsAdmin)
admin.site.register(BitbakeVersion, BitbakeVersionAdmin)
admin.site.register(Release, ReleaseAdmin)
admin.site.register(ToasterSetting, ToasterSettingAdmin)
