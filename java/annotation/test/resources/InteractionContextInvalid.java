package annotation.interaction;

import com.cfa.sts.integration.common.annotation.Interaction;
import com.cfa.sts.integration.common.enums.AdapterEnum;
import com.cfa.sts.integration.common.enums.DirectionEnum;
import com.cfa.sts.integration.common.enums.FunctionEnum;
import com.cfa.sts.integration.common.enums.TransportTypeEnum;

public class InteractionContextInvalid {

    @Interaction(
            direction = DirectionEnum.IN,
            context = "Invalid:Context",
            function = FunctionEnum.CONFIRM,
            transport = TransportTypeEnum.HTTP)
    public void testMethod() {

    }
    @Interaction(
            direction = DirectionEnum.IN,
            context = "#Special|Chars",
            function = FunctionEnum.CONFIRM,
            transport = TransportTypeEnum.HTTP)
    public void testMetho2() {  }
}