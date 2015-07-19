package net.bytebuddy.implementation.bytecode.member;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.generic.GenericTypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.test.utility.MockitoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodInvocationGenericTest {

    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDeclaredForm declaredMethod;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private GenericTypeDescription methodReturnType, declaredReturnType;

    @Mock
    private TypeDescription declaredErasure, declaringType, targetType, otherType;

    @Mock
    private MethodDescription.Token methodToken;

    @Before
    public void setUp() throws Exception {
        when(methodDescription.asDeclared()).thenReturn(declaredMethod);
        when(methodDescription.getReturnType()).thenReturn(methodReturnType);
        when(declaredMethod.getReturnType()).thenReturn(declaredReturnType);
        when(declaredReturnType.asRawType()).thenReturn(declaredErasure);
        when(declaredMethod.getDeclaringType()).thenReturn(declaringType);
        when(declaringType.asRawType()).thenReturn(declaringType);
        when(declaredMethod.asToken()).thenReturn(methodToken);
        when(declaredMethod.isSpecializableFor(targetType)).thenReturn(true);
    }

    @Test
    public void testGenericMethod() throws Exception {
        TypeDescription genericErasure = mock(TypeDescription.class);
        when(methodReturnType.asRawType()).thenReturn(genericErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription);
        assertThat(stackManipulation.isValid(), is(true));
        assertThat(stackManipulation, is((StackManipulation) new MethodInvocation.OfGenericMethod(genericErasure, MethodInvocation.invoke(declaredMethod))));
    }

    @Test
    public void testGenericMethodErasureEqual() throws Exception {
        when(methodReturnType.asRawType()).thenReturn(declaredErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription);
        assertThat(stackManipulation.isValid(), is(true));
        assertThat(stackManipulation, is((StackManipulation) MethodInvocation.invoke(declaredMethod)));
    }

    @Test
    public void testGenericMethodVirtual() throws Exception {
        TypeDescription genericErasure = mock(TypeDescription.class);
        when(methodReturnType.asRawType()).thenReturn(genericErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).virtual(targetType);
        assertThat(stackManipulation.isValid(), is(true));
        assertThat(stackManipulation, is((StackManipulation) new StackManipulation.Compound(MethodInvocation.invoke(declaredMethod).virtual(targetType),
                TypeCasting.to(genericErasure))));
    }

    @Test
    public void testGenericMethodVirtualErasureEqual() throws Exception {
        when(methodReturnType.asRawType()).thenReturn(declaredErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).virtual(targetType);
        assertThat(stackManipulation.isValid(), is(true));
        assertThat(stackManipulation, is(MethodInvocation.invoke(declaredMethod).virtual(targetType)));
    }

    @Test
    public void testGenericMethodSpecial() throws Exception {
        TypeDescription genericErasure = mock(TypeDescription.class);
        when(methodReturnType.asRawType()).thenReturn(genericErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).special(targetType);
        assertThat(stackManipulation.isValid(), is(true));
        assertThat(stackManipulation, is((StackManipulation) new StackManipulation.Compound(MethodInvocation.invoke(declaredMethod).special(targetType),
                TypeCasting.to(genericErasure))));
    }

    @Test
    public void testGenericMethodSpecialErasureEqual() throws Exception {
        when(methodReturnType.asRawType()).thenReturn(declaredErasure);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).special(targetType);
        assertThat(stackManipulation.isValid(), is(true));
        assertThat(stackManipulation, is(MethodInvocation.invoke(declaredMethod).special(targetType)));
    }

    @Test
    public void testGenericMethodDynamic() throws Exception {
        TypeDescription genericErasure = mock(TypeDescription.class);
        when(methodReturnType.asRawType()).thenReturn(genericErasure);
        when(declaredMethod.isBootstrap()).thenReturn(true);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).dynamic(FOO,
                otherType,
                Collections.<TypeDescription>emptyList(),
                Collections.emptyList());
        assertThat(stackManipulation.isValid(), is(true));
        assertThat(stackManipulation, is(MethodInvocation.invoke(declaredMethod).dynamic(FOO,
                otherType,
                Collections.<TypeDescription>emptyList(),
                Collections.emptyList())));
    }

    @Test
    public void testGenericMethodDynamicErasureEqual() throws Exception {
        when(methodReturnType.asRawType()).thenReturn(declaredErasure);
        when(declaredMethod.isBootstrap()).thenReturn(true);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).dynamic(FOO,
                otherType,
                Collections.<TypeDescription>emptyList(),
                Collections.emptyList());
        assertThat(stackManipulation.isValid(), is(true));
        assertThat(stackManipulation, is(MethodInvocation.invoke(declaredMethod).dynamic(FOO,
                otherType,
                Collections.<TypeDescription>emptyList(),
                Collections.emptyList())));
    }

    @Test
    public void testIllegal() throws Exception {
        TypeDescription genericErasure = mock(TypeDescription.class);
        when(methodReturnType.asRawType()).thenReturn(genericErasure);
        when(declaredMethod.isTypeInitializer()).thenReturn(true);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription);
        assertThat(stackManipulation.isValid(), is(false));
    }
}
