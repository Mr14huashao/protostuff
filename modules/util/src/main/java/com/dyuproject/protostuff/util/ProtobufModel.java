//================================================================================
//Copyright (c) 2009, David Yu
//All rights reserved.
//--------------------------------------------------------------------------------
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 1. Redistributions of source code must retain the above copyright notice,
//    this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 3. Neither the name of protostuff nor the names of its contributors may be used
//    to endorse or promote products derived from this software without
//    specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
//================================================================================

package com.dyuproject.protostuff.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David Yu
 * @created Aug 24, 2009
 */

public class ProtobufModel
{
    
    private ModelMeta _modelMeta;
    private Property[] _props;
    private Map<String, Property> _propMap;
    
    public ProtobufModel(ModelMeta modelMeta)
    {
        _modelMeta = modelMeta;
        _props = new Property[_modelMeta.getMaxNumber()+1];
        _propMap = new HashMap<String,Property>(modelMeta.getPropertyCount());
        
        for(PropertyMeta pm : modelMeta.getPropertyMetaMap().values())
        {
            Property prop = new Property(pm);
            _props[pm.getNumber()] = prop;
            _propMap.put(pm.getName(), prop);
        }
    }
    
    public ModelMeta getModelMeta()
    {
        return _modelMeta;
    }
    
    public Property getProperty(int num)
    {
        return _props[num];
    }
    
    public Property getProperty(String name)
    {
        return _propMap.get(name);
    }
    
    
    public static class Property
    {
        
        private PropertyMeta _propertyMeta;
        private MessagePropertyAccessor _messageAccessor;
        private BuilderPropertyAccessor _builderAccessor;
        
        Property(PropertyMeta propertyMeta)        
        {
            _propertyMeta = propertyMeta;
            _messageAccessor = new MessagePropertyAccessor(propertyMeta);
            _builderAccessor = new BuilderPropertyAccessor(propertyMeta);
        }
        
        public PropertyMeta getMeta()
        {
            return _propertyMeta;
        }
        
        public MessagePropertyAccessor getMessageField()
        {
            return _messageAccessor;
        }
        
        public BuilderPropertyAccessor getBuilderField()
        {
            return _builderAccessor;
        }

        public Object getValue(Object target) 
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
        {
            if(_propertyMeta.getMessageClass()==target.getClass())
                return _messageAccessor.getValue(target);
            else if(_propertyMeta.getBuilderClass()==target.getClass())
                return _builderAccessor.getValue(target);
            return null;
        }
        
        public Object removeValue(Object target) 
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
        {
            if(_propertyMeta.getMessageClass()==target.getClass())
                return _messageAccessor.removeValue(target);
            else if(_propertyMeta.getBuilderClass()==target.getClass())
                return _builderAccessor.removeValue(target);
            return null;
        }
        
        public void setValue(Object target, Object value) 
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
        {
            if(_propertyMeta.getMessageClass()==target.getClass())
                _messageAccessor.setValue(target, value);
            else if(_propertyMeta.getBuilderClass()==target.getClass())
                _builderAccessor.setValue(target, value);
        }
        
        public boolean replaceValueIfNone(Object target, Object value) 
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
        {
            if(_propertyMeta.getMessageClass()==target.getClass())
                return _messageAccessor.replaceValueIfNone(target, value);
            else if(_propertyMeta.getBuilderClass()==target.getClass())
                return _builderAccessor.replaceValueIfNone(target, value);
            return false;
        }
        
        public Object replaceValueIfAny(Object target, Object value) 
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
        {
            if(_propertyMeta.getMessageClass()==target.getClass())
                return _messageAccessor.replaceValueIfAny(target, value);
            else if(_propertyMeta.getBuilderClass()==target.getClass())
                return _builderAccessor.replaceValueIfAny(target, value);
            return null;
        }
        
    }

}
