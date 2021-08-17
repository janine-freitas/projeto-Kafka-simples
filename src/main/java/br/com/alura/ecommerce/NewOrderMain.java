package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String [] args) throws ExecutionException, InterruptedException {
        //ENVIA MENSAGEM
        try(var dispatcher = new KafkaDispatcher()){
            for(var i = 0; i < 10; i++) {
                var key = UUID.randomUUID().toString();
                var value = key + ", 5000, 70000";
                dispatcher.send("ECOMMERCE_NEW_ORDER", key, value);

                var email = "Thank you for your order. We are processing your order.";
                var emailRecord = new ProducerRecord<String, String>("ECOMMERCE_SEND_EMAIL", key, email);
                dispatcher.send("ECOMMERCE_SEND_EMAIL", key, value);
            }
        }
    }
    private static Callback getCallback() {
        return (data, ex) -> {
            if(ex != null){
                ex.printStackTrace();
                return;
            } else {
                System.out.println("sucesso "+data.topic()+":::"+data.partition()+":::"+data.offset());
            }
        };
    }


}
