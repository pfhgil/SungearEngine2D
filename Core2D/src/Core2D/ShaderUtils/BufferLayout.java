package Core2D.ShaderUtils;

import org.lwjgl.opengl.GL20C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL33C.glVertexAttribDivisor;

public class BufferLayout
{
    // лист аттрибутов
    private List<VertexAttribute> attributes;
    // stride - шаг для каждой вершины
    private int stride = 0;

    public BufferLayout(VertexAttribute... _attributes)
    {
        // новый лист
        attributes = new ArrayList<VertexAttribute>();

        // помещаю аттрибуты из _attributes в attributes
        attributes.addAll(Arrays.asList(_attributes));

        calcOffsetAndStride();

        _attributes = null;
    }

    // вычисляю offset и stride
    public void calcOffsetAndStride()
    {
        int offset = 0;
        stride = 0;

        for(VertexAttribute vertexAttribute : attributes) {
            // устанавливаю offset для аттрибута
            vertexAttribute.setOffset(offset);

            offset += vertexAttribute.getSize();
            stride += vertexAttribute.getSize();
        }
    }

    // добавить все аттрибуты сразу
    public void addAllAttributes(boolean divisor)
    {
        for(VertexAttribute attribute : attributes) {
            addAttribute(attribute, divisor);
        }
    }
    // добавить один аттрибут
    public void addAttribute(VertexAttribute attribute, boolean divisor)
    {
        GL20C.glEnableVertexAttribArray(attribute.getID());
        GL20C.glVertexAttribPointer(
                attribute.getID(),
                attribute.getElementAttributeSize(),
                attribute.getConvertedShaderDataType(),
                attribute.isNormalized(),
                stride,
                attribute.getOffset()
        );

        if(divisor) {
            glVertexAttribDivisor(attribute.getID(), 1);
        }
    }

    public void destroy()
    {
        if(attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                attributes.get(i).destroy();
                attributes.set(i, null);
            }

            attributes.clear();
        }
        attributes = null;
    }

    // геттеры
    public List<VertexAttribute> getAttributes() { return attributes; }

    public int getStride() { return stride; }
}
