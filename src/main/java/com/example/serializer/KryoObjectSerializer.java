package com.example.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.cglib.CGLibProxySerializer;
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * Created by sagar on 30/3/17.
 */
public class KryoObjectSerializer implements ApplicationContextAware,RedisSerializer {

    //TODO Move this class to com.dt.dtcore.serializer

    private final static Logger LOGGER = Logger.getLogger(KryoObjectSerializer.class.getName());

    @Autowired
    org.springframework.context.ApplicationContext applicationContext;

    ThreadLocal kryoLocal = new ThreadLocal();

    public Kryo getKryo() {
        Object value = this.kryoLocal.get();
        if (value == null) {
            value = new Kryo();
            initialiseNewKryo((Kryo) value);
            this.kryoLocal.set(value);
        }
        return (Kryo) value;
    }

    // Configure Default Kryo Object-- STARTS--
    void initialiseNewKryo(Kryo kryo) {
        registerDefaultType(kryo);
        registerCustomType(kryo);
    }

    public void registerDefaultType(Kryo kryo) {

        kryo.setRegistrationRequired(false);

        kryo.setClassLoader(applicationContext.getClassLoader());
//        LOGGER.info(Thread.currentThread().getId() + "grailsApplication.classLoader assigned to kryo.classLoader");

//        FieldSerializer flashScopeSerializer = new FieldSerializer(kryo, GrailsFlashScope.class);
//        kryo.register(GrailsFlashScope.class, flashScopeSerializer);
//        DefaultSerializers.LocaleSerializer localeSerializer = new DefaultSerializers.LocaleSerializer();
//        kryo.register(java.util.Locale.class, localeSerializer);

//        kryo.setInstantiatorStrategy(new com.esotericsoftware.shaded.org.objenesis.strategy.StdInstantiatorStrategy());
//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));


        kryo.addDefaultSerializer(Enum.class, DefaultSerializers.EnumSerializer.class);
//        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        kryo.register(Collections.EMPTY_LIST.getClass(), new DefaultSerializers.CollectionsEmptyListSerializer());
        kryo.register(Collections.EMPTY_MAP.getClass(), new DefaultSerializers.CollectionsEmptyMapSerializer());
        kryo.register(Collections.EMPTY_SET.getClass(), new DefaultSerializers.CollectionsEmptySetSerializer());
        kryo.register(Collections.singletonList("").getClass(), new DefaultSerializers.CollectionsSingletonListSerializer());
        kryo.register(Collections.singleton("").getClass(), new DefaultSerializers.CollectionsSingletonSetSerializer());
        kryo.register(Collections.singletonMap("", "").getClass(), new DefaultSerializers.CollectionsSingletonMapSerializer());
        kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
//        kryo.register(InvocationHandler.class, new JdkProxySerializer());
//        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
//        SynchronizedCollectionsSerializer.registerSerializers(kryo);

// custom serializers for non-jdk libs

// register CGLibProxySerializer, works in combination with the appropriate action in handleUnregisteredClass (see below)
        kryo.register(CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer());
// joda DateTime, LocalDate and LocalDateTime
//        kryo.register(DateTime.class, new JodaDateTimeSerializer());
    }

    public void registerCustomType(Kryo kryo) {
//        kryo.register(CommonResponseDTO.class);
    }

    public byte[] serialize(Object obj) throws SerializationException {
        byte[] bytes = null;
        try {
            if (obj != "" && obj != null) {
                bytes = write(obj);
            }
        } catch (Exception e) {
            throw new SerializationException("failed to serialize object of type- ${obj.class}", e);
        }
        return bytes;
    }

    public Object deserialize(byte[] bytes) throws SerializationException {
        Object obj = null;
        if ((bytes != null) && (bytes.length > 0)) {
            obj = read(bytes);
        }
        return obj;
    }

    byte[] write(Object obj) {
        Kryo kryo = getKryo();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        kryo.writeClassAndObject(output, obj);
        output.close();
        byte[] bytes = outputStream.toByteArray();
        return bytes;
    }

    Object read(byte[] bytes) {
        Kryo kryo = getKryo();
        Input input = new Input(new ByteArrayInputStream(bytes));
        Object deserializeObject = kryo.readClassAndObject(input);
        return deserializeObject;
    }

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
