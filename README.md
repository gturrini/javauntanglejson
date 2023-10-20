# javaUntangleJSON

## About
It's a java Untangler library with 2 main methods available:

### untangle
This method gets a JSON construct as input (in String format) and flattens it from any nesting or array it may contain.

Per each 'nested' object it concatenates the parent key to the item key
Per each 'array' object it adds one new row to the result per each of the array items.

Then it returns the flatten JSON in an ArrayList<String>

#### Example
Sending as an input the JSON:

```
{
    "id": "ID001",
    "Seller": {
        "CompanyName": "Bergstrom Inc",
        "Email": "ashlea.bartell@bergstrominc.com",
        "Address": "648 Shelby View, 77785",
        "ZipCode": "36041-0295",
        "State": "Oregon",
        "City": "Andersonhaven"
    },
    "TelephoneNumber": [ "(269) 660-8831", "(269) 660-8832", "(269) 660-8833" ]
}
```

Will get as a result:

```
{
    "id": "ID001",
    "Seller.CompanyName": "Bergstrom Inc",
    "Seller.Email": "ashlea.bartell@bergstrominc.com",
    "Seller.Address": "648 Shelby View, 77785",
    "Seller.ZipCode": "36041-0295",
    "Seller.State": "Oregon",
    "Seller.City": "Andersonhaven",
    "TelephoneNumber": "(269) 660-8831"
}
{
    "id": "ID001",
    "Seller.CompanyName": "Bergstrom Inc",
    "Seller.Email": "ashlea.bartell@bergstrominc.com",
    "Seller.Address": "648 Shelby View, 77785",
    "Seller.ZipCode": "36041-0295",
    "Seller.State": "Oregon",
    "Seller.City": "Andersonhaven",
    "TelephoneNumber": "(269) 660-8832"
}
{
    "id": "ID001",
    "Seller.CompanyName": "Bergstrom Inc",
    "Seller.Email": "ashlea.bartell@bergstrominc.com",
    "Seller.Address": "648 Shelby View, 77785",
    "Seller.ZipCode": "36041-0295",
    "Seller.State": "Oregon",
    "Seller.City": "Andersonhaven",
    "TelephoneNumber":  "(269) 660-8833"
}
```

### untangleWithFilter

This method works like the previous one.

In addition it has a filter as input param that is used to filter what JSON constructs to return.

The filter is built providing key, value and comparison operator.

Valid comparison operators are:
- ==
- !=
- <
- <=
- &gt;
- &gt;=
- like
- !like

## Usage
Clone or download this repository.

You have then to integrate with your projects where you want to 'untangle' any JSON construct. See code below:
```
package org.javauntanglejson;

import org.json.JSONException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String jsonString="{yadda:[{\"alfa\":\"A\",\"beta\":2,\"gamma\":[3,33]},{\"alfa\":\"B\",\"beta\":3,\"gamma\":[4, 44]}]}";

        try {
            Untangler uu = new Untangler();
            //filter fo = new filter("batters.batter.id", "==", "1001");
            filter fo = new filter("scores", "==", "1");
            ArrayList<String> myArrayF = uu.untangleWithFilter(jsonString, fo);
            for (String jsonObject: myArrayF) {
                System.out.println(jsonObject);
            }
            //ArrayList<String> myArray = uu.untangleJSON(jsonString);
            //for (String jsonObject: myArray) {
            //    System.out.println(jsonObject);
            //}
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
```

## Support
I am happy for you to reach out anytime. Send emails to: gturrini@amazon.co.uk

## Roadmap
I am planning to extend the filtering capabilities so that multiple key/values/comparison operators can be used as filters.

Feel free to reach out with suggestions for prioritization or new ideas.

## Security

See [CONTRIBUTING](./CONTRIBUTING.md#security-issue-notifications) for more information.

## Code of Conduct

See [CODE OF CONDUCT](./CODE_OF_CONDUCT.md) for more information.

## License

This library is licensed under the MIT-0 License. See the [LICENSE](./LICENSE) file.