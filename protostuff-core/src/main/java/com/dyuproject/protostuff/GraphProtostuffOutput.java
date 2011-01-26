//========================================================================
//Copyright 2007-2011 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.protostuff;

import static com.dyuproject.protostuff.WireFormat.WIRETYPE_END_GROUP;
import static com.dyuproject.protostuff.WireFormat.WIRETYPE_REFERENCE;
import static com.dyuproject.protostuff.WireFormat.WIRETYPE_START_GROUP;
import static com.dyuproject.protostuff.WireFormat.makeTag;

import java.io.IOException;
import java.util.IdentityHashMap;

/**
 * A ProtostuffOutput w/c can handle cyclic dependencies when serializing 
 * objects with graph transformations.
 *
 * @author David Yu
 * @created Dec 10, 2010
 */
public final class GraphProtostuffOutput extends FilterOutput<ProtostuffOutput>
{
    
    private final IdentityHashMap<Object,Integer> references;
    private int refCount = 0;
    
    public GraphProtostuffOutput(ProtostuffOutput output)
    {
        super(output);
        references = new IdentityHashMap<Object,Integer>();
    }
    
    public GraphProtostuffOutput(ProtostuffOutput output, int initialCapacity)
    {
        super(output);
        references = new IdentityHashMap<Object,Integer>(initialCapacity);
    }

    public <T> void writeObject(int fieldNumber, T value, Schema<T> schema, 
            boolean repeated) throws IOException
    {
        final ProtostuffOutput output = this.output;
        
        final Integer last = references.get(value);
        if(last == null)
        {
            references.put(value, refCount++);
            
            output.tail = output.sink.writeVarInt32(
                    makeTag(fieldNumber, WIRETYPE_START_GROUP), 
                    output, 
                    output.tail);
            
            schema.writeTo(this, value);
            
            output.tail = output.sink.writeVarInt32(
                    makeTag(fieldNumber, WIRETYPE_END_GROUP), 
                    output, 
                    output.tail);
            return;
        }
        
        output.tail = output.sink.writeVarInt32(
                last.intValue(), 
                output, 
                output.sink.writeVarInt32(
                        makeTag(fieldNumber, WIRETYPE_REFERENCE), 
                        output, 
                        output.tail));
    }
    
    
}