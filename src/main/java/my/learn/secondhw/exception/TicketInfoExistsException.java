package my.learn.secondhw.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TicketInfoExistsException extends RuntimeException {
    public TicketInfoExistsException(String msg) {
        super(msg);
    }
}
