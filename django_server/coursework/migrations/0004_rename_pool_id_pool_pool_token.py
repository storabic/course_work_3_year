# Generated by Django 3.2.6 on 2021-08-22 17:07

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('coursework', '0003_auto_20210822_1644'),
    ]

    operations = [
        migrations.RenameField(
            model_name='pool',
            old_name='pool_id',
            new_name='pool_token',
        ),
    ]
