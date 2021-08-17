package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String [] args) throws ExecutionException, InterruptedException {
        //ENVIA MENSAGEM
        try(var orderDispatcher = new KafkaDispatcher<Order>()){
            try(var emailDispatcher = new KafkaDispatcher<String>()) {
                for (var i = 0; i < 10; i++) {
                    var userId = UUID.randomUUID().toString();
                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 500 + 1);
                    Order order = new Order(userId, orderId, amount);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", userId, order);

                    var email = "Thank you for your order. We are processing your order.";
                    var emailRecord = new ProducerRecord<String, String>("ECOMMERCE_SEND_EMAIL", userId, email);
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userId, email);
                }
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
