import glob
import os
import subprocess

def write_html_beg(html_file):
    html_file.write(
        '''
            <html>
                <head>
                    <style>
                        * {
                            box-sizing: border-box;
                            margin: 0px;
                            padding: 0px;
                        }

                        th {
                            padding: 5px;
                            text-align: left;
                            vertical-align: middle;
                        }

                        .failure-text {
                            color: red;
                        }

                        ul {
                            list-style-position: inside;
                        }
                    </style>
                </head>
                <body>
                    <table border="1">
                        <thead>
                            <tr>
                                <th>Test Name</th>
                                <th>Result</th>
                                <th>Details</th>
                            </tr>
                        </thead>
                        <tbody>
        '''
    )

def write_html_test_pass(html_file, test_file_name):
    html_file.write(
        f'''
            <tr>
                <th>{test_file_name}</th>
                <th>Pass</th>
                <th></th>
            </tr>
        '''
    )

def write_html_test_fail(html_file, test_file_name, source_file_name, expect_file_name, result_file_name):
    html_file.write(
        f'''
            <tr class='failure-text'>
                <th>{test_file_name}</th>
                <th>Fail</th>
                <th>
                    <ul>
                        <li><a href="./{source_file_name}">./{source_file_name}</li>
                        <li><a href="./{expect_file_name}">./{expect_file_name}</li>
                        <li><a href="./{result_file_name}">./{result_file_name}</li>
                    </ul>
                </th>
            </tr>
        '''
    )

def write_html_end(html_file):
    html_file.write(
        '''
                        </tbody>
                    </table>
                </body>
            </html>
        '''
    )

def get_source_and_expect(test_file_name):
        source = ''
        expect = ''
        with open(test_file_name, 'r') as test_file:
            hasReadExpect = False
            for line in test_file:
                if line == '---* EXPECT *---\n':
                    hasReadExpect = True
                    continue
                if not hasReadExpect:
                    source += line
                else:
                    expect += line
        return (source, expect)

def get_output(source_file_name):
        jvm = 'C:\\Users\\angus\\.jdks\\openjdk-24.0.1\\bin\\java.exe'
        return subprocess.run(
            [jvm, '-cp', '..\\target\\classes\\', 'com.colossalg.Jocks', source_file_name],
            capture_output=True,
            text=True
        ).stdout

def run_tests():

    html_file = open('test_results.html', 'w')
    write_html_beg(html_file)

    for test_file_name in glob.glob('*.test'):
        source, expect = get_source_and_expect(test_file_name)

        source_file_name = test_file_name + '.source'
        with open(source_file_name, 'w') as source_file:
            source_file.write(source)

        result = get_output(source_file_name)

        expect_file_name = test_file_name + '.expect'
        result_file_name = test_file_name + '.result'
        if result == expect:
            write_html_test_pass(html_file, test_file_name)
            for file_name in [source_file_name, expect_file_name, result_file_name]:
                if os.path.exists(file_name):
                    os.remove(file_name)
        else:
            write_html_test_fail(html_file, test_file_name, source_file_name, expect_file_name, result_file_name)
            with open(expect_file_name, 'w') as expect_file:
                expect_file.write(expect)
            with open(result_file_name, 'w') as result_file:
                result_file.write(result)
    
    write_html_end(html_file)
    html_file.close()


if __name__ == '__main__':
    run_tests()