package payroll;

public class OrderNotFoundException extends RuntimeException {
    OrderNotFoundException (Long id){
      super("Order number "+ id +" not found");
      //TODO advice
    }
}
