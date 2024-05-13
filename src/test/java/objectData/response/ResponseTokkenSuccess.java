package objectData.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseTokkenSuccess {

    @JsonProperty("token")
    private String token;
    @JsonProperty("expires")
    private String expires;
    @JsonProperty("status")
    private String status;
    @JsonProperty("result")
    private String result;

}
