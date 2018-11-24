import configparser
import json
import sys
import os
def check_exist():
    """检测所需文件是否存在"""
    if os.path.isfile('config.ini'):
        return True
    else:
        return False

def get_config():
    configInstance = configparser.ConfigParser()
    if not check_exist():
        print('run init first')
        exit()
    configInstance.read('config.ini')
    return configInstance
    pass

def get_config_json():
    pass

def init():
    configInstance = configparser.ConfigParser()
    configInstance["target"] = {}
    configInstance["target"]["platform"]  = sys.platform
    
    with open('config.ini', 'w') as configfile:
        configInstance.write(configfile)
    print('done')
