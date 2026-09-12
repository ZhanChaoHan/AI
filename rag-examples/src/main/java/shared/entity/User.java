package shared.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * @author zhanchaohan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	private Long userId;
	private String userName;
	private char gender;
}
