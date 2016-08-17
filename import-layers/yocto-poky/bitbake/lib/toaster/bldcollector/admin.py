from django.contrib import admin
from django.contrib.admin.filters import RelatedFieldListFilter
from orm.models import BitbakeVersion, Release, LayerSource, ToasterSetting
from django.forms.widgets import Textarea
from django import forms
import django.db.models as models

from django.contrib.admin import widgets, helpers

class LayerSourceAdmin(admin.ModelAdmin):
    pass

class BitbakeVersionAdmin(admin.ModelAdmin):

    # we override the formfield for db URLField because of broken URL validation

    def formfield_for_dbfield(self, db_field, **kwargs):
        if isinstance(db_field, models.fields.URLField):
            return forms.fields.CharField()
        return super(BitbakeVersionAdmin, self).formfield_for_dbfield(db_field, **kwargs)



class ReleaseAdmin(admin.ModelAdmin):
    pass

class ToasterSettingAdmin(admin.ModelAdmin):
    pass

admin.site.register(LayerSource, LayerSourceAdmin)
admin.site.register(BitbakeVersion, BitbakeVersionAdmin)
admin.site.register(Release, ReleaseAdmin)
admin.site.register(ToasterSetting, ToasterSettingAdmin)
