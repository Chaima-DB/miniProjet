package ma.dbibih.miniprojet.utilities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BatchResponse {
    private int savedUsers = 0;
    private int nonSavedUsers =0;

    public BatchResponse(int savedUsers, int nonSavedUsers) {
        this.savedUsers = savedUsers;
        this.nonSavedUsers = nonSavedUsers;
    }
}